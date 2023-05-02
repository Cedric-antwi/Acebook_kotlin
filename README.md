# Acebook Kotlin Template

_Coaching this? Find the source
[here.](https://github.com/makersacademy/slug/blob/main/materials/universe/acebook/seeds/kotlin-http4k/README.ed.md)_

## Technologies

Here's an overview of the technologies used to build this template application. 
You don't need to do a deep dive on each one right now. Instead, try to get a feeling for 
the big picture and then dive into the details when a specific task pushes you in that direction.

This template app was built using:
 * Kotlin
 * http4k for the HTTP webserver and routing
 * ktorm to integrate with the PostgreSQL database
 * Handlebars for views templating
 * JUnit and hamkrest for testing

## Current features
- Users can sign up, sign in and log out
- The page to create a new post is implemented, but submitting it is not yet done
- Users have to be signed in to create posts

## How to setup and run

### Opening the project codebase

You'll need to IntelliJ editor. If not installed already, head to https://www.jetbrains.com/idea/download/#section=mac
and download the Community Edition.

Once installed, open the project directory into IntelliJ.

**If you're getting an error** such as "Invalid Gradle JDK configuration found" in the bottom section of the editor, you might need to click "Change JDK location"
then select the correct JDK (Java Development Kit) before building the project again.

### Setup the database

This project uses PostgreSQL as a database. You'll need to install this first.

Create two PostgreSQL databases `acebook_kotlin` and `acebook_kotlin_test`.

In both databases, import the SQL dump in the file `resources/database.sql`.

### Running the app (dev environment)

Run the configuration "Acebook (Dev)" in the top-left corner of IntelliJ.

This should run the server on port 9000 (you can leave it running in the background).

Head to http://localhost:9000 to browse the app.

### Running the app (test environment)

Run the configuration "Acebook (Test)" in the top-left corner of IntelliJ.

This should run the server on port 9999 (you can leave it running in the background).

### Running the tests

First, make sure you've run the app server in test environment as explained above.

Then, use IntelliJ to run tests in the `test` directory.

You can do this by right-clicking on the `test` directory in the Project explorer, then choosing "Run 'Tests in..."

(You can also run individual tests using IntelliJ).

### Using the app

Once the app is running in dev environment, you can head to http://localhost:9000/users/new to create a new account.

Once this is done, you can sign in using the same credentials on http://localhost:9000/sessions/new

The list of posts (index page) is at http://localhost:9000.

## What are the different files?
 - `App.kt` contains the http4k app with the routing - see https://www.http4k.org/guide/howto/simple_routing/
 - The `handlers` directory contains handler functions (they receive a `Request` and need to return a `Response`)
 - The `entities` directory contains entities (models) - see https://www.ktorm.org/en/entities-and-column-binding.html#Define-Entities
 - The `schemas` directory contains database schema definition code - see https://www.ktorm.org/en/schema-definition.html 
 - The `viewmodels` directory contains "ViewModel" classes - see https://www.http4k.org/guide/reference/templating/
 - The `resources` directory contains Handlebars templates - see https://mustache.github.io/
 - `Main.kt` contains the `main` function running the app as a server

## Useful doc links

*http4k*:
https://www.http4k.org/blog/meet_http4k/
https://www.http4k.org/guide/concepts/lens/
https://www.http4k.org/guide/howto/use_html_forms/

*handlebars*:

*ktorm*



<!-- BEGIN GENERATED SECTION DO NOT EDIT -->

---

**How was this resource?**  
[😫](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=README.md&prefill_Sentiment=😫) [😕](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=README.md&prefill_Sentiment=😕) [😐](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=README.md&prefill_Sentiment=😐) [🙂](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=README.md&prefill_Sentiment=🙂) [😀](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=README.md&prefill_Sentiment=😀)  
Click an emoji to tell us.

<!-- END GENERATED SECTION DO NOT EDIT -->
