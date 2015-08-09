## Sangria playground

This is an example of [GraphQL](https://facebook.github.io/graphql) server written with [Play framework](https://www.playframework.com) and
[Sangria](http://sangria-graphql.org). It also serves as a playground - on the right hand side you see a textual representation of the GraphQL
schema which is implemented on the server and you can query here. On the left hand side
you can execute a GraphQL queries and see the results of it's execution.

This is just a small demonstration. It really gets interesting when you start to play with the schema on the server side. Fortunately it's
pretty easy to do. Asince it's a simple Play application, all it takes to start playground locally and start playing with schema is this:

```bash
$ git clone https://github.com/sangria-graphql/sangria-playground.git
$ cd sangria-playground
$ sbt run
```

Now you are ready to point your browser to [http://localhost:9000](http://localhost:9000) to see the same page you are seeing here.
The only prerequisites are [SBT](http://www.scala-sbt.org/download.html) and [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
Playground also available as a [Typesafe Activator Template](https://www.typesafe.com/activator/template/sangria-playground).