package wappi.iRoadCommunityPolicing.Utils;

/**
 * Created by Coze on 5/6/2014.
 */


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.widget.ActivityChooserView;
import android.support.v7.widget.ShareActionProvider;
import android.view.View;

import com.wappi.iRoadCommunityPolicing.R;

public class CustomShareActionProvider extends ShareActionProvider {

    private final Context mContext;

    public CustomShareActionProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        ActivityChooserView chooserView =
                (ActivityChooserView) super.onCreateActionView();

        // Set your drawable here
        Drawable icon =
                mContext.getResources().getDrawable(R.drawable.ic_share1);

        chooserView.setExpandActivityOverflowButtonDrawable(icon);

        return chooserView;
    }
}