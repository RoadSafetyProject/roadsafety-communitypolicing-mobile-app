package wappi.iRoadCommunityPolicing.ServerCommunication;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
    public  static final String SERVER_URL = "http://192.168.43.18/dvs.lab4change.com";

    // Google project id
    public static final String SENDER_ID = "337494783388";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "MSchedule";

    public static final String DISPLAY_MESSAGE_ACTION ="com.mschedule.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
