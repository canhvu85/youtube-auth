# youtube-auth
Android Module for Youtube Sign-In with Channel selection (like Youtube Android App)

1. Clone or download module.
2. Import module into project:
   Android Studio -> File -> New -> Import module -> Select directory with source of downloaded module.
3. Add module to dependencies in the app level build.gradle:
   ```
   compile project(':youtube-auth')
   ```

4. Start activity to Sign-In:
```java
   Intent intent = new Intent(this, YoutubeAuthActivity.class);
   intent.putExtra(YoutubeAuthActivity.KEY_APP_CLIENT_ID, <OAUTH_CLIENT_ID_FROM_GOOGLE_DEV_CONSOLE>);
   intent.putExtra(YoutubeAuthActivity.KEY_APP_THEME_RES_ID, R.style.AppTheme_NoActionBar); //optional
   intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_TITLE_RES_ID, R.string.app_name_youtube_auth); //optional
   intent.putExtra(YoutubeAuthActivity.KEY_APPBAR_HOME_ICON_RES_ID, R.drawable.ic_close_24dp); //optional
   startActivityForResult(intent, YoutubeAuthActivity.REQUEST_CODE);
```

5. Handle result:
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case YoutubeAuthActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    //this is a token for using in requests to Youtube Data API
                    if (data != null && data.getExtras().containsKey(YoutubeAuthActivity.KEY_RESULT_TOKEN)) {
                        mToken = data.getStringExtra(YoutubeAuthActivity.KEY_RESULT_TOKEN);
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errMessage = data != null ? data.getStringExtra(YoutubeAuthActivity.KEY_RESULT_ERROR) : "Cancelled";
                    Toast.makeText(this, errMessage, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }
```
