package org.tatu.bugrap.DistributionBar;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.events.MouseEventDetails;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DistributionBar extends Component {
    private List<DistributionBarClickListener> clickListeners;
    private final DistributionBarServerRpc serverRpc;

    public DistributionBar() {
        this(2);
    }

    public DistributionBar(int numberOfParts) {
        this.clickListeners = new ArrayList();
        this.serverRpc = new DistributionBarServerRpc() {
            public void onItemClicked(int index, MouseEventDetails mouseEventDetails) {
                DistributionBarClickEvent event = new DistributionBarClickEvent(DistributionBar.this, index, mouseEventDetails);
                Iterator var4 = DistributionBar.this.clickListeners.iterator();

                while(var4.hasNext()) {
                    DistributionBarClickListener listener = (DistributionBarClickListener)var4.next();
                    listener.onDistributionBarClicked(event);
                }

            }
        };
    }

    public DistributionBar(double[] sizes) {
        this(sizes.length);
        this.updatePartSizes(sizes);
    }

    public DistributionBarState getState() {
        return (DistributionBarState)getState();
    }

    public DistributionBarState getState(boolean markDirty) {
        return (DistributionBarState)getState(markDirty);
    }

    public void updatePartSizes(double[] partSizes) {
        for(int i = 0; i < this.getNumberOfParts() && i < partSizes.length; ++i) {
            ((DistributionBarState.Part)this.getState().getParts().get(i)).setSize(partSizes[i]);
        }

    }

    public void setupPart(int index, double size, String tooltip) {
        this.setupPart(index, size, tooltip, (String)null);
    }

    public void setupPart(int index, double size, String tooltip, String styleName) {
        DistributionBarState.Part part = (DistributionBarState.Part)this.getState().getParts().get(index);
        part.setSize(size);
        part.setTooltip(tooltip);
        part.setTooltip(styleName);
    }

    public DistributionBar setPartSize(int index, double size) {
        if (size < 0.0D) {
            throw new IllegalArgumentException("Size must be zero or larger");
        } else {
            ((DistributionBarState.Part)this.getState().getParts().get(index)).setSize(size);
            return this;
        }
    }

    public DistributionBar setPartSize(int index, double size, String caption) {
        if (size < 0.0D) {
            throw new IllegalArgumentException("Size must be zero or larger (" + size + ")");
        } else {
            DistributionBarState.Part part = (DistributionBarState.Part)this.getState().getParts().get(index);
            part.setSize(size);
            part.setCaption(caption);
            return this;
        }
    }

    public double getPartSize(int index) throws IndexOutOfBoundsException {
        return ((DistributionBarState.Part)this.getState(false).getParts().get(index)).getSize();
    }

    public DistributionBar setPartTitle(int index, String title) {
        ((DistributionBarState.Part)this.getState().getParts().get(index)).setTitle(title);
        return this;
    }

    public String getPartCaption(int index) {
        return ((DistributionBarState.Part)this.getState(false).getParts().get(index)).getTitle();
    }

    public DistributionBar setPartCaption(int index, String caption) {
        ((DistributionBarState.Part)this.getState().getParts().get(index)).setCaption(caption);
        return this;
    }

    public String getPartTitle(int index) {
        return ((DistributionBarState.Part)this.getState().getParts().get(index)).getTitle();
    }

    public DistributionBar setPartTooltip(int index, String tooltip) {
        ((DistributionBarState.Part)this.getState(false).getParts().get(index)).setTooltip(tooltip);
        return this;
    }

    public DistributionBar setPartStyleName(int index, String styleName) {
        ((DistributionBarState.Part)this.getState().getParts().get(index)).setStyleName(styleName);
        return this;
    }

    public int getNumberOfParts() {
        return this.getState().getParts().size();
    }

    private void changeStatePartsSize(int newSize) {
        while(this.getState().getParts().size() < newSize) {
            this.getState().getParts().add(new DistributionBarState.Part());
        }

        while(this.getState().getParts().size() > newSize) {
            this.getState().getParts().remove(newSize);
        }

    }

    public void setNumberOfParts(int numberOfParts) {
        if (numberOfParts > 1 && this.getNumberOfParts() != numberOfParts) {
            this.changeStatePartsSize(numberOfParts);
        }

    }

    public void addDistributionBarClickListener(DistributionBarClickListener listener) {
        if (this.clickListeners.isEmpty()) {
            this.getState().sendClicks = true;
        }

        this.clickListeners.add(listener);
    }

    public void removeDistributionBarClickListener(DistributionBarClickListener listener) {
        this.clickListeners.remove(listener);
        if (this.clickListeners.isEmpty()) {
            this.getState().sendClicks = false;
        }

    }

    public void setZeroSizedVisible(boolean zeroVisible) {
        this.getState().zeroVisible = zeroVisible;
    }

    public boolean isZeroSizedVisible() {
        return this.getState().zeroVisible;
    }

    public void setMinPartWidth(double pixels) {
        this.getState().minWidth = pixels;
    }

    public double getMinPartWidth() {
        return this.getState().minWidth;
    }
}

