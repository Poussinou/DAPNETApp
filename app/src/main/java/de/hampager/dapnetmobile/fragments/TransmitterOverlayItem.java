package de.hampager.dapnetmobile.fragments;


/**
 * Created by schwarz on 27.10.17.
 */


public class TransmitterOverlayItem {
    private boolean wideRangeUsage;
    private boolean online;

    public TransmitterOverlayItem(String aTitle, String aSnippet ,boolean wideRangeUsage, boolean online) {
        this.wideRangeUsage=wideRangeUsage;
        this.online=online;
    }
}
