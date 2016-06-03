package org.libss.logic.guice

import java.util.concurrent.atomic.AtomicReference

import com.google.inject.multibindings.Multibinder
import com.google.inject.{AbstractModule, Guice, Injector, Module}

import scala.annotation.tailrec

/**
  * date: 02.06.2016 23:44
  * author: Kaa
  *
  * This object holds guice injector and provides some ways of its reconfiguration
  */
object InjectorHolder {
  private var modules: Seq[Module] = Seq()

  @tailrec
  private[guice] def installModule(module: Module) {
    val oldInjector = injector.get
    modules = modules :+ module
    if (!injector.compareAndSet(oldInjector, Guice.createInjector(modules: _*))) installModule(module)
  }

  private[guice] val injector: AtomicReference[Injector] = new AtomicReference[Injector]

  def injectorSafeGet = injector.get.ensuring(_ != null, "Injector wasn't set. Are you forgot to declare InjectionInitializer?")
}

trait InjectionConfigurator {
  def install(module: Module) {
    InjectorHolder.installModule(module)
  }

  def installAll(modules: Seq[Module]) {
    modules.foreach(install)
  }
}

/**
  * Just mix this trait to any class and use @Inject annotations on any class's field!
  */
trait Injection {
  private val injector = InjectorHolder.injectorSafeGet
  injector.injectMembers(this)
}

/**
  * Guice module extension for sets collections binding
  */
abstract class ExtendedAbstractModule extends AbstractModule {
  protected def bindSet[T](classes: Iterable[Class[_ <: T]], bindToClass: Class[T]) {
    val multiBinder = Multibinder.newSetBinder(binder, bindToClass)
    classes.foreach(clazz => multiBinder addBinding() to clazz)
  }
}