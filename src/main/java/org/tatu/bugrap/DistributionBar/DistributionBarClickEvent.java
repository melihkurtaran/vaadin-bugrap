package org.tatu.bugrap.DistributionBar;

import com.vaadin.flow.component.charts.events.MouseEventDetails;

public class DistributionBarClickEvent {
    private final DistributionBar distributionBar;
    private final int partIndex;
    private final MouseEventDetails details;

    public DistributionBarClickEvent(DistributionBar distributionBar, int clickIndex, MouseEventDetails details) {
        this.distributionBar = distributionBar;
        this.partIndex = clickIndex;
        this.details = details;
    }

    public DistributionBar getDistributionBar() {
        return this.distributionBar;
    }

    public int getPartIndex() {
        return this.partIndex;
    }

    public int getClientX() {
        return null != this.details ? this.details.getAbsoluteX() : -1;
    }

    public int getClientY() {
        return null != this.details ? this.details.getAbsoluteY() : -1;
    }

    public boolean isAltKey() {
        return null != this.details ? this.details.isAltKey() : false;
    }

    public boolean isCtrlKey() {
        return null != this.details ? this.details.isCtrlKey() : false;
    }

    public boolean isMetaKey() {
        return null != this.details ? this.details.isMetaKey() : false;
    }

    public boolean isShiftKey() {
        return null != this.details ? this.details.isShiftKey() : false;
    }
}
