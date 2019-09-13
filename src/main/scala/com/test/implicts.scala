/*
 * Copyright (C) 2017-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test

import java.security.KeyStore.PasswordProtection
import java.util
import hobby.chenai.nakam.basis.TAG.ClassName
import hobby.chenai.nakam.lang.J2S._
import hobby.chenai.nakam.util.SeqOps._
import javax.security.auth
import org.bitcoinj.core._
import org.bitcoinj.crypto.HDUtils
import org.spongycastle.util.encoders.Hex

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 21/09/2017
 */
object implicts {
  lazy val UNKNOWN_PUBKEY_HASH = new Array[Byte](20)

  implicit class CryptoBytes(bytes: Array[Byte]) {
    //    @inline def toBase64(flags: Int = Base64.NO_WRAP) = Base64.encodeToString(bytes, flags)

    @inline def toBase58 = Base58.encode(bytes)

    def withChecksum(version: Int = -1): Array[Byte] = {
      val lenVer: Int = if (version < 0) 0 else {
        require(version >= 0 && version < 256)
        1
      }
      // Copy from `Address.toBase58`.
      // A stringified buffer is:
      //   1 byte version + data bytes + 4 bytes check code (a truncated hash)
      val addressBytes = new Array[Byte](lenVer + bytes.length + 4)
      if (lenVer > 0) addressBytes(0) = version.toByte
      System.arraycopy(bytes, 0, addressBytes, lenVer, bytes.length)
      val checksum = Sha256Hash.hashTwice(addressBytes, 0, bytes.length + lenVer)
      System.arraycopy(checksum, 0, addressBytes, bytes.length + lenVer, 4)
      addressBytes
    }

    /**
     * @param version 若 `< 0`，则表示不加入版本号（默认），否则视为版本号。
     *                不过对于 QtumMainNet, 应该填写 `netParams.main.getAddressHeader`。
     * @see withChecksum(Int)
     */
    def toBase58WithChecksum(version: Int = -1): String = withChecksum(version).toBase58

    def verifyChecksum: Boolean = bytes.take(bytes.length - 4).toHash256Twice.take(4) sameElements bytes.takeRight(4)

    //    @inline def decodeBase64(flags: Int = Base64.NO_WRAP) = Base64.decode(bytes, flags)

    @inline def toHash256 = Sha256Hash.hash(bytes)

    @inline def toHash256Twice = Sha256Hash.hashTwice(bytes)

    /** 地址的生成算法见：`VersionedChecksummedBytes(super of org.bitcoinj.core.Address)`。 */
    @inline def sha256hash160 = Utils.sha256hash160(bytes)

    // TODO: javax.crypto.Mac
    @inline def hmacSha512(key: Array[Byte]) = HDUtils.hmacSha512(key, bytes)

    @inline def toHex = Hex.toHexString(bytes)
  }

  implicit class CryptoString(baseXx: String) {
    //    @inline def decodeBase64(flags: Int = Base64.NO_WRAP) = Base64.decode(baseXx, flags)

    @inline def decodeBase58 = Base58.decode(baseXx)

    /**
     * 主要用于加入了校验码的地址，与 `toBase58WithChecksum` 对应。
     * 注意：这里并没有去掉在 `toBase58WithChecksum` 里面加入的版本号。若要去掉，可在本返回值后面加上 `.tail` 函数。
     */
    @throws[AddressFormatException]
    @inline def decodeBase58Checked = Base58.decodeChecked(baseXx)

    @throws[AddressFormatException]
    @inline def decodeBase58ToBigInt = Base58.decodeToBigInteger(baseXx)

    /** `LegacyAddress.toBase58()` 即可得到地址字符串。还有个`SegwitAddress`，需要时再处理。 */
    @inline def base58Address(net: NetworkParameters): Address = Address.fromString(net, baseXx)

    @inline def decodeHex: Array[Byte] = Hex.decode(baseXx)
  }

  implicit class ImplAddress(address: Address) {
    def isUnknown: Boolean = address.getHash sameElements UNKNOWN_PUBKEY_HASH
  }

  implicit class ECKey2Address(ecKey: ECKey) {
    def toAddress(net: NetworkParameters): LegacyAddress = LegacyAddress.fromPubKeyHash(net, ecKey.getPubKeyHash)

    def toAddressSegwit(net: NetworkParameters): SegwitAddress = SegwitAddress.fromKey(net, ecKey)
  }

  implicit class Protection(val bytes: Array[Byte]) extends auth.Destroyable with Equals with Cloneable with ClassName {
    require(bytes.nonNull)

    def ptc = this

    override def clone(): Protection = {
      if (bytes.isEmpty) Protection.nil else {
        require(!isDestroyed)
        bytes.clone()
      }
    }

    override def destroy(): Unit = {
      val (b, tag) = if (isDestroyed) (false, null) else (true, className)
      util.Arrays.fill(bytes, Protection.zero)
      //      if (b) w("[销毁]len: %d, data: %s", bytes.length, bytes.mkString$.s)(tag)
    }

    override def isDestroyed = bytes.forall(_ == Protection.zero)

    override def equals(o: scala.Any) = o match {
      case that: Protection if that.canEqual(this) => that.ensuring(!_.isDestroyed)
        .bytes sameElements this.ensuring(!_.isDestroyed).bytes
      case _ => false
    }

    override def canEqual(that: Any) = that.isInstanceOf[Protection]

    override def hashCode() = Option(this.ensuring(!_.isDestroyed).bytes).fold(0)(_.hashCode)
  }

  object Protection {
    private val zero = 0.toByte
    lazy val nil = new Protection(Array.empty) {
      override def isDestroyed = false

      override def clone() = this
    }
  }

  implicit class ProtectionBits(val bits: Array[Boolean]) extends auth.Destroyable with Equals with Cloneable with ClassName {
    require(bits.nonNull)

    def ptc = this

    override def clone(): ProtectionBits = {
      if (bits.isEmpty) ProtectionBits.nil else {
        require(!isDestroyed)
        bits.clone()
      }
    }

    override def destroy(): Unit = {
      val (b, tag) = if (isDestroyed) (false, null) else (true, className)
      util.Arrays.fill(bits, ProtectionBits.zero)
//      if (b) w("[销毁]len: %d, data: %s", bits.length, bits.mkString.s)(tag)
    }

    override def isDestroyed = bits.forall(_ == false)

    override def equals(o: scala.Any) = o match {
      case that: Protection if that.canEqual(this) => that.ensuring(!_.isDestroyed)
        .bytes sameElements this.ensuring(!_.isDestroyed).bits
      case _ => false
    }

    override def canEqual(that: Any) = that.isInstanceOf[Protection]

    override def hashCode() = Option(this.ensuring(!_.isDestroyed).bits).fold(0)(_.hashCode)
  }

  object ProtectionBits {
    private val zero = false
    lazy val nil = new ProtectionBits(Array.empty) {
      override def isDestroyed = false

      override def clone() = this
    }
  }

  implicit class Password(chars: Array[Char]) extends PasswordProtection(chars: Array[Char]) with Equals with Cloneable with ClassName {
    // 在父类里面被 clone 了，所以这里要直接把原始的销毁。
    if (chars.nonNull) util.Arrays.fill(chars, '\u0000')

    def pwd = this

    override def clone(): Password = {
      val pwd = getPassword
      if (pwd.isNull || pwd.isEmpty) Password.nil else {
        require(!isDestroyed)
        pwd.clone()
      }
    }

    def isValid: Boolean = {
      val pwd = getPassword.ensuring(!isDestroyed)
      pwd.nonNull && pwd.nonEmpty && pwd.forall(isVisibleChar)
    }

    /** 密码的健壮程度。递增，最高无限，越低表示越不合格，最小为`0`。 */
    def strongth: Float = {
      val pwd = getPassword.ensuring(!isDestroyed).toSeq
      var score = 0d
      if (pwd.nonEmpty) {
        val N = 6d
        val lad = pwd.ladderCount()
        score += (if (lad._1 > 0) math.log(lad._3 + 1) / math.log('~' - '!' + 1) else 1 / N) * pwd.length

        def _msc(seq: Seq[Char], fac: Double = 1) = {
          val sf = for (c <- pwd if seq.contains(c)) yield c
          (if (sf.isEmpty) 0 else {
            val disl: Double = sf.distinct.length
            (math.pow(2, disl / sf.length) - 1) * sf.length / disl // 减缓跌幅
          }) * sf.length * fac
        }

        val _AZ = 'A' to 'Z'
        val _az = 'a' to 'z'
        val _09 = '0' to '9'
        val _xx = ('!' to '~').filterNot { c =>
          _AZ.contains(c) || _az.contains(c) || _09.contains(c)
        }
        val _AZCts = _AZ containsAnyOf pwd
        val _azCts = _az containsAnyOf pwd
        val _09Cts = _09 containsAnyOf pwd
        val _xxCts = _xx containsAnyOf pwd

        var complex = 0d
        if (_AZCts) complex += 1
        if (_azCts) complex += 1
        if (_09Cts) complex += 1
        if (_xxCts) complex += 1
        complex /= 4d

        score += _msc(_AZ,.8 * complex)
        score += _msc(_az,.3 * complex)
        score += _msc(_09,.3 * complex)
        score += _msc(_xx, 1 * complex)
      }
      score.toFloat
    }

    def toBytes: Array[Byte] = if (this eq Password.nil) Array(0) else {
      // 注意这里不要用`!_.isDestroyed`, 这个情况很微妙，又进行了一次 implicit, 永远返回 true。
      // 但如果把`getPassword`放在后面，情况完全不同。
      val chars = getPassword.ensuring(!isDestroyed)
      val bytes = new Array[Byte](chars.length * 2)
      for (i <- chars.indices) {
        bytes(i * 2) = (chars(i) >> 8 & 0xff).toByte
        bytes(i * 2 + 1) = (chars(i) & 0xff).toByte
      }
      bytes
    }

    // ASCii 字符序：
    // ! " # $ % & ' ( ) * + , - . /
    // 0 1 2 3 4 5 6 7 8 9 : ; < = > ? @
    // A B C D E F G H I J K L M N O P Q R S T U V W X Y Z [ \ ] ^ _ `
    // a b c d e f g h i j k l m n o p q r s t u v w x y z { | } ~
    // TODO: 应该改为可以支持任何语言的可见字符而不只是英文才对。同时上面的`strongth()`函数也要改。
    def isVisibleChar(c: Char): Boolean = c >= '!' && c <= '~'

    override def destroy(): Unit = {
      val (pwd, tag) = if (isDestroyed) (null, null) else (Option(getPassword).getOrElse(Array()), className)
      super.destroy()
      //      if (pwd.nonNull) w("[销毁密码]len: %d, data: %s", pwd.length, pwd.mkString$.s)(tag)
    }

    override def equals(o: scala.Any) = o match {
      case that: Password if that.canEqual(this) => that.ensuring(!_.isDestroyed)
        .getPassword sameElements this.ensuring(!_.isDestroyed).getPassword
      case _ => false
    }

    override def canEqual(that: Any) = that.isInstanceOf[Password]

    override def hashCode() = Option(this.ensuring(!_.isDestroyed).getPassword).fold(0)(_.hashCode)
  }

  object Password {
    lazy val nil = new Password(null) {
      override def getPassword = null // 父类有 flag 检查导致的异常。
      override def isDestroyed = false

      override def clone() = this
    }
  }
}
