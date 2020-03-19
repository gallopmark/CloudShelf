package tv.lib.ijkplayer;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public interface ThumbnailLoader {
    void display(@NonNull Context context, @NonNull ImageView imageView, @NonNull String url);
}
