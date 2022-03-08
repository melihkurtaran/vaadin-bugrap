package org.tatu.bugrap.DistributionBar;

// import com.vaadin.shared.AbstractComponentState;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DistributionBarState  // extends AbstractComponentState
{
    public boolean sendClicks = false;
    private List<DistributionBarState.Part> parts = new ArrayList();
    public boolean zeroVisible = true;
    public double minWidth = 30.0D;

    public DistributionBarState() {
    }


    public DistributionBarState getState()
    {
        return this;
    }
    public List<DistributionBarState.Part> getParts() {
        return this.parts;
    }

    public void setParts(List<DistributionBarState.Part> parts) {
        this.parts = parts;
    }

    public static class Part implements Serializable {
        private double size;
        private String caption;
        private String title;
        private String tooltip;
        private String styleName;

        public Part() {
            this.title = new String();
            this.tooltip = new String();
        }

        public Part(int size) {
            this.setSize((double)size);
            this.title = new String();
        }

        public void setSize(double size) {
            this.size = size;
        }

        public double getSize() {
            return this.size;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getCaption() {
            return this.caption;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public String getStyleName() {
            return this.styleName;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }
    }
}
