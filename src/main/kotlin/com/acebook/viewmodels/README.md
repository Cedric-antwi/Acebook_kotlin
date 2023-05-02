# View-Models

View-models are data classes used to "share" some data between the Kotlin code run 
within http4k handler functions and the Handlebars templates. They're called view-models 
as they make a "bridge" between the models (our program's data) and the views.

For example, the function `indexHandler` creates a new `FeedViewModel` object to hold 
the list of posts fetched from the database. It inherits from the class `ViewModel` in http4k.

```kotlin
data class FeedViewModel(val posts: List<Post>, val currentUser: User?) : ViewModel
```


Properties on this object (here, `posts` and `currentUser`) 
can be accessed from the Handlebars template `resources/com.acebook.viewmodels/FeedViewModel.hbs`.

```html
<h1>Welcome to Acebook</h1>

{{#if currentUser}}
    <p>Currently logged in as {{currentUser.email}}</p>
    <p>
        <a href="/sessions/clear" >Sign out</a>
    </p>
{{/if}}

<h3>Latest posts</h3>

{{#each posts}}
    <div class="post">{{content}}</div>
{{/each}}
```

When creating a new template for a new page, it's likely you will need to also create a new `ViewModel` data class.

The template file name needs to have the same name as the view-model class. 



<!-- BEGIN GENERATED SECTION DO NOT EDIT -->

---

**How was this resource?**  
[😫](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fviewmodels%2FREADME.md&prefill_Sentiment=😫) [😕](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fviewmodels%2FREADME.md&prefill_Sentiment=😕) [😐](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fviewmodels%2FREADME.md&prefill_Sentiment=😐) [🙂](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fviewmodels%2FREADME.md&prefill_Sentiment=🙂) [😀](https://airtable.com/shrUJ3t7KLMqVRFKR?prefill_Repository=makersacademy%2Facebook-kotlin-http4k-template&prefill_File=src%2Fmain%2Fkotlin%2Fcom%2Facebook%2Fviewmodels%2FREADME.md&prefill_Sentiment=😀)  
Click an emoji to tell us.

<!-- END GENERATED SECTION DO NOT EDIT -->
