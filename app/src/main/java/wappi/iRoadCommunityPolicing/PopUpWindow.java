package wappi.iRoadCommunityPolicing;

/**
 * Created by Ilakoze on 5/2/2015.
 */


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.wappi.iRoadCommunityPolicing.R;

public class PopUpWindow implements InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;

    public PopUpWindow(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup, null);
        }

        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());

        String [] snip = marker.getSnippet().split(",");
        tv=(TextView)popup.findViewById(R.id.email);
        tv.setText(snip[0]);

        tv=(TextView)popup.findViewById(R.id.phone_number);
        tv.setText(snip[1]);

        tv=(TextView)popup.findViewById(R.id.alt_phone_number);
        tv.setText(snip[2]);

        return(popup);
    }
}