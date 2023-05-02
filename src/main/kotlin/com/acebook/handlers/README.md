# The Request-Response cycle in http4k

The library http4k has two Kotlin types to represent HTTP requests and responses — unsurprisingly, 
their names is `Request` and `Response`.

https://www.http4k.org/guide/concepts/http/

A handler function takes one `Request` object in argument, and needs to return a `Response`. 
The work it does then depends on what kind of handler it is — it could interact with a database, perform some 
validation, etc.

For example, `indexHandler` returns a lambda which takes a `Request` and returns a `Response` after loading 
the list of posts from the database.

```kotlin
// Posts.kt

fun indexHandler(contexts: RequestContexts): HttpHandler = { request: Request ->
    val posts = database.sequenceOf(Posts).toList()
    val currentUser: User? = contexts[request]["user"]
    val viewModel = FeedViewModel(posts, currentUser)

    Response(Status.OK)
        .body(templateRenderer(viewModel))
}
```


Wait — why is it not written like this?

```kotlin
fun indexHandler(request: Request): Response = {
    val posts = database.sequenceOf(Posts).toList()
    val currentUser: User? = contexts[request]["user"]
    val viewModel = FeedViewModel(posts, currentUser)

    return Response(Status.OK)
        .body(templateRenderer(viewModel))
}
```

@TODO

<!-- BEGIN GENERATED SECTION DO NOT EDIT -->

---

**How was this resource?**  
[😫](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fhandlers%2FREADME.md&prefill_Sentiment=😫) [😕](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fhandlers%2FREADME.md&prefill_Sentiment=😕) [😐](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fhandlers%2FREADME.md&prefill_Sentiment=😐) [🙂](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fhandlers%2FREADME.md&prefill_Sentiment=🙂) [😀](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fhandlers%2FREADME.md&prefill_Sentiment=😀)  
Click an emoji to tell us.

<!-- END GENERATED SECTION DO NOT EDIT -->
