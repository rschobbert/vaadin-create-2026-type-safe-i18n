# "A Type Safe I18N Solution with Vaadin Flow"

## Rüdiger Schobbert

### at VaadinCreate 2026

---

### Demo Project 10 (demo-10-simple-vaadin14-java8)

This project is a demo project for my talk at the "Vaadin Create 2026" conference.

You might ask, "why the hell would you take vaadin 14 and java 8 ?"

Well the short answer is: my talk is about a self-written framework I use in a productive application for a customer. And that application is still on vaadin 14 and java 8.
So the first step was to peal the framework out of the original code. And I wanted to make as few changes as possible.

But you might as well look at the next demo project (demo-15-...) which is almost exactly the same as this one, but modernized for vaadin 25 and java 21.
There you will also find a more detailed description of the project.


The long answer is: I am still on vaadin 14, because that customer is still working with weblogic 12 which limits java to version 8 and vaadin to version 14. Work is ongoing in that company to
migrate all applications to weblogic 14, and hopefully soon to weblogic 15. But both Weblogic 14.1.2 and 15.1.1 still limit vaadin to version 23, which is a pitty.
