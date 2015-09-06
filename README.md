dependency-grapher
==================

Quick hack to add maven dependency trees and the actual class file references to a graph database.

Currently supports maven dependency resolution and OrientDB.

The graph created follows this structure:

![Graph model](graph.png)

Build using:

    $ ./gradlew installDist

An executable will be generated in `dependency-grapher-cli/build/install/dependency-grapher-cli/bin` and can be invoked like so:

    $ ./dependency-grapher-cli config.yml

Config example:

    database:
      connector: "orientdb"
      url: "remote:127.0.0.1/test"
      username: "root"
      password: "correct horse battery staples"
    resolver:
      localRepository:
        location: "/tmp/grapher-repo"
      remoteRepositories:
        - id: "Central"
          contentType: "default"
          location: "http://repo1.maven.org/maven2/"
    applications:
    - name: "spring-orm"
      artifacts:
      - "org.springframework:spring-orm:4.2.0.RELEASE"




Some queries to try out:

Number of classes per artifact:

    SELECT label, $x, $y
    FROM ArtifactInstance
    LET $x=in_ExportedBy.out.in_ImplementedBy.out,
        $y=$x.size()
    ORDER BY $y ASC

Referenced classes with no implementation on the dependency tree:

    SELECT name
    FROM Class
    WHERE out_ImplementedBy.in IS NULL

Classes with multiple implementations:

    SELECT name, $implementations.label
    FROM Class
    LET $implementations = out_ImplementedBy.in.out_ExportedBy.in
    WHERE coalesce($implementations.size(), 0) > 1
    ORDER BY name ASC



License
-------

This software is licensed under the [ISC](http://opensource.org/licenses/ISC) license:

    Copyright (c) 2015 Henrik Gustafsson <henrik.gustafsson@fnord.se>

    Permission to use, copy, modify, and distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

