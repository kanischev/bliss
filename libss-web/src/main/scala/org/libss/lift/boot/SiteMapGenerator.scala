package org.libss.lift.boot

import com.google.inject.Inject
import net.liftweb.http.{NotFoundResponse, S}
import net.liftweb.sitemap.Loc.If
import net.liftweb.sitemap.{Loc, SiteMap}
import org.libss.lift.security.LoginRedirect
import org.libss.logic.security.{RoleId, UserService}

/**
  * Created by Kaa 
  * on 12.06.2016 at 20:44.
  */
trait SiteMapGenerator {
  def siteMap: SiteMap
}

trait SecuredSiteMapGenerator extends SiteMapGenerator {
  @Inject(optional = true)
  protected var userService: UserService[_] = _

  def visibleFor(roleId: RoleId): Loc.If = If(() => userService.isUserInRole(roleId), () => S.request.map(_.createNotFound).getOrElse(NotFoundResponse()))
  def visibleFor(roles: Seq[RoleId]): Loc.If = If(() => roles.exists(r => userService.isUserInRole(r)), () => S.request.map(_.createNotFound).getOrElse(NotFoundResponse()))
  def invisibleForAuthorized: Loc.If = If(() => !userService.isLoggedIn, () => S.redirectTo("/"))

  def authRequired: Loc.If = If(() => userService.isLoggedIn, () => {
    LoginRedirect.set(S.uriAndQueryString)
    S.redirectTo(loginPageUrl)
  })

  def loginPageUrl: String
}