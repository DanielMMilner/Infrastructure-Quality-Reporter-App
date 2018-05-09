package s3542977.com.tqr;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapMarkers implements ClusterItem {
    private final LatLng mPosition;
    private final String quality;
    private final String type;

    MapMarkers(LatLng mPosition, String mTitle, String mSnippet) {
        this.mPosition = mPosition;
        this.quality = mTitle;
        this.type = mSnippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return quality;
    }

    @Override
    public String getSnippet() {
        return type;
    }
}
