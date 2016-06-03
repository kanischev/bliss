LIBSS FRAMEWORK
==============

LIBSS is Lift, Bootstrap, Liquibase, Squeryl, Scala

Ready to use solution for rapid web development with LIFT framework and other frameworks in tuned environment.
Provides CRUD functionality and conventions on the whole APP structure.

#Why?
1. Because LIBSS provides mature stack for web-application development.
2. Because Lift is powerful web-framework and LIBSS can provide reusable components to get maximum result with minimum efforts
3. Because LIBSS offers simple and transparent solution for forms creation with support for validation and custom form input fields, submit prevention etc. with no boilerplate

#The purpose:
To create easy-to-use LIFT-based web-framework, with infrastructure for fast setup and run complex web-applications.

#Modules
    *libss-utils*       - contains reusable helpers trait, classes etc.
    *libss-logic*       - basic logic module helpers
    *libss-web*         - common Lift-based stuff for web applications
    *libss-runner*      - Jetty runner helper

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
