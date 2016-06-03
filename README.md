BLISS FRAMEWORK
==============

BLISS is Bootstrap, Lift, Liquibase, Squeryl, Scala

Ready to use solution for rapid web development with LIFT framework and other frameworks in tuned environment.
Provides CRUD functionality and conventions on the whole APP structure.

#Why?
Because Lift's CRUD mechanisms are not perfect as for me.
I prefer to separate models from views (there should not be view-annotations in model classes)
You can't create forms for case classes easily with support for validation and custom form input fields, submit prevention etc.
All that boilerplate simplified by BLISS

#The purpose:
To create scala LIFT-based webframework, providing infrastructure for fast setup and running LIFT-based application with additional functionality that LIFT lacks out-of-the-box.

#Modules
    *bliss-utils*       - contains reusable helpers trait, classes etc.
    *bliss-logic*       - basic logic module helpers
    *bliss-web*         - common Lift-based stuff for web applications
    *bliss-runner*      - Jetty runner helper

#Uses:
    *Liquibase*         - for DB migrations
    *Squeryl*           - as default project ORM (could be replaced)
    *Lift*              - as web framework
    *Guice*             - as IOC container
    *Jetty*             - as environment to run during development (Ready to use runnable class)
    *Scalate*           - as template engine to use with Lift for some reasons
    *Twitter Bootstrap* - as styling framework
    *JQuery*            - as JavaScript lib
    *JQuery and Bootstrap plugin*s - to create reusable components in UI
