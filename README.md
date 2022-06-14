# Swipe Animated  Button
Swipe button for Kotlin Android with a loading dots bounce progress bar for async operations

# Gradle
```
dependencies {
	        implementation 'com.github.maxwell-kimaiyo:Swipe-Animated-Button:0.1.0'
	}
```

# Usage
1. In your XML layout file, add this custom view
```xml
  <com.maxwell.swipeanimatedbutton.SwipeButton
        android:id="@+id/swipe_btn"
        android:layout_width="match_parent"
        android:layout_height="48dp" />
```

2. React to successful swipe on the button by adding a swipe listener
```kotlin
        binding?.swipeBtn?.setTextSize(60)
        binding?.swipeBtn?.setText(context.getString(R.string.txt_continue))
        binding?.swipeBtn?.setInstText(context.getString(R.string.txt_swipe_the_btn))
        binding?.swipeBtn?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        binding?.swipeBtn?.setOnSwipeListener(object : SwipeButton.OnSwipeListener {
        override fun onSwipeConfirm() {
           binding?.swipeBtn?.showResultIcon(isSuccess = true, shouldReset = true)
        }
        })

```

3. After the async task is completed, tell the SwipeButton to show a result icon.
   Either a tick for a successful async operation or cross for a failed async operation.

```kotlin
        binding?.swipeBtn?.showResultIcon(isSuccess = true, shouldReset = true) //if task succeeds
        binding?.swipeBtn?.showResultIcon(isSuccess = false, shouldReset = true) //if task fails
```

4. You can disable or enable the swipe button

```kotlin
        binding?.swipeBtn?.isEnabled = true //to enable swipe button
        binding?.swipeBtn?.isEnabled = false //to disable swipe button
```


Feel free to raise feature requests via the issue tracker for more customizations or just send in a PR :)

# Sample
Clone the repository and check out the `app` module.

# License

```
MIT License

Copyright (c) 2022 Maxwell Kimaiyo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```