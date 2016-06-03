package ru.softband.util.security

import ru.softband.util.helpers.PasswordHasher

/**
  * date: 03.06.2016 00:05
  * author: Kaa
  *
  * User service abstracts
  */

trait UserCredentialsChecker[T <: User] extends PasswordHasher {
  //TODO: Add disabled user check
  protected def checkLoginAllowed(userLogin: String): Boolean = {
    userAccountBy(userLogin).map(checkLoginAllowed(_)).getOrElse(false)
  }

  protected def checkLoginAllowed(user: T) = user.isConfirmed

  def checkCredentials(login: String, password: String): Boolean = {
    userAccountBy(login).map(checkCredentials(_, password)).getOrElse(false)
  }

  protected def checkCredentials(user: T, pwd: String): Boolean = user.getPasswordHash == generateHash(pwd, Option(user.getPasswordSalt))

  def userAccountBy(login: String): Option[T]
}

trait UserService[T <: User] extends UserCredentialsChecker[T] {
  def logUserIn(userLogin: String, userPass: String): Option[AuthenticationError]
  def getUserLogin: Option[String]
  def isUserInRole(role: RoleId): Boolean
  def isLoggedIn = getUserLogin.isDefined
  def getCurrentUser: Option[T]
  def doWithRoles[A](fun: () => A, roles: Seq[RoleId]): A
}

case class AuthenticationError(message: String)

case class RoleId(roleName: String)