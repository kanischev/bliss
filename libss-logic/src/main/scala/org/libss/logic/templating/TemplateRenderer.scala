package org.libss.logic.templating

import com.google.inject.Inject
import java.util.Date

import org.libss.logic.security.UserService
import org.libss.util.helpers.GenericBuilder

/**
 * date: 26.07.12
 * author: Kaa
 */

trait TemplateParamBuilder extends GenericBuilder[TemplateParamBuilder] {
  def addParamIf(ifFun: () => Boolean, paramName: String, paramValue: Any): TemplateParamBuilder
  def addParam(paramName: String, paramValue: Any): TemplateParamBuilder
  def addParamsMap(fewParamsMap: Map[String, Any]): TemplateParamBuilder
}

case class TemplateParamBuilderImpl(params: Map[String, Any]) extends TemplateParamBuilder {
  def addParam(paramName: String, paramValue: Any) = copy(params + (paramName -> paramValue))

  def addParamsMap(fewParamsMap: Map[String, Any]) = copy(params ++ fewParamsMap)

  def addParamIf(ifFun: () => Boolean, paramName: String, paramValue: Any) = {
    if (ifFun()) copy(params + (paramName -> paramValue))
    else this
  }
}

trait TemplateRenderer {
  def templateParamBuilder: TemplateParamBuilder
  def renderTemplate[T](template: String, builderWithParams: TemplateParamBuilder)(implicit typedTemplateRenderer: TypedTemplateRenderer[T]): T
  def renderTemplate[T](templateId: String, templateSource: String, builderWithParams: TemplateParamBuilder)(implicit typedTemplateRenderer: TypedTemplateRenderer[T]): T
}

class TemplateRendererImpl extends TemplateRenderer {
  @Inject
  protected var userService: UserService[_] = _

  def templateParamBuilder = new TemplateParamBuilderImpl(Map("currentUser" -> userService.getCurrentUser,
                                                              "currentDate" -> new Date()))

  def renderTemplate[T](templateId: String, builderWithParams: TemplateParamBuilder)(implicit typedTemplateRenderer: TypedTemplateRenderer[T]) = {
    typedTemplateRenderer.renderTemplate(templateId, builderWithParams.asInstanceOf[TemplateParamBuilderImpl].params)
  }

  override def renderTemplate[T](templateId: String, templateSource: String, builderWithParams: TemplateParamBuilder)(implicit typedTemplateRenderer: TypedTemplateRenderer[T]) = {
    typedTemplateRenderer.renderTemplate(templateId, templateSource, builderWithParams.asInstanceOf[TemplateParamBuilderImpl].params)
  }
}

trait TypedTemplateRenderer[T] {
  def renderTemplate(templateId: String, params: Map[String, Any]): T
  def renderTemplate(templateId: String, templateText: String, params: Map[String, Any]): T
}
