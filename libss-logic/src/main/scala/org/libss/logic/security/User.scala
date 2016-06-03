package org.libss.logic.security

import org.libss.util.helpers.{Gender, PasswordHasher, PasswordGenerator}

/**
  * date: 02.06.2016 23:59
  * author: Kaa
  *
  * Replacement for Lift's LiftMegaFuc@#ngSuperDuperUser
  */
trait User {
  def userIdAsString: String
  def getLogin: String
  def getEmail: String
  def getFirstName: String
  def getLastName: String
  def getPasswordHash: String
  def getPasswordSalt: String
  def isSuperUser: Boolean
  def isConfirmed: Boolean
  def getAvatar: Option[Array[Byte]]
  def getGender: Option[Gender.Gender]

  def niceName: String = (getFirstName, getLastName, getEmail) match {
    case (f, l, e) if f.length > 1 && l.length > 1 => f+" "+l+" ("+e+")"
    case (f, _, e) if f.length > 1 => f+" ("+e+")"
    case (_, l, e) if l.length > 1 => l+" ("+e+")"
    case (_, _, e) => e
  }

  /**
   * Get a short name for the user
   */
  def shortName: String = (getFirstName, getLastName) match {
    case (f, l) if f.length > 1 && l.length > 1 => f+" "+l
    case (f, _) if f.length > 1 => f
    case (_, l) if l.length > 1 => l
    case _ => getEmail
  }
}

case class PasswordGeneratedUser(user: User)
  extends User
    with PasswordHasher
    with PasswordGenerator {

  private lazy val salt = generateSalt
  lazy val pass = generatePassword()

  def userIdAsString = user.userIdAsString
  def getLogin = user.getLogin
  def getEmail = user.getEmail
  def getFirstName = user.getFirstName
  def getLastName = user.getLastName
  def getPasswordHash = generateHash(pass, Some(getPasswordSalt))
  def getPasswordSalt = salt
  def isSuperUser = user.isSuperUser
  def isConfirmed = user.isConfirmed
  def getAvatar = user.getAvatar
  def getGender = user.getGender
}
