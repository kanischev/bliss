package org.libss.logic.security

import org.libss.util.helpers.PasswordHasher

/**
  * date: 03.06.2016 00:05
  * author: Kaa
  *
  * User service abstracts
  */

trait UserCredentialsChecker[T <: User] extends PasswordHasher {
  //TODO: Add disabled user check
  protected def checkLoginAllowed(userLogin: String): Boolean = {
    userAccountBy(userLogin).exists(checkLoginAllowed(_))
  }

  protected def checkLoginAllowed(user: T) = user.isConfirmed

  def checkCredentials(login: String, password: String): Boolean = {
    userAccountBy(login).exists(checkCredentials(_, password))
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