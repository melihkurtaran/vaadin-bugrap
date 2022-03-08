package org.tatu.bugrap.DistributionBar;

import com.vaadin.flow.component.charts.events.MouseEventDetails;
import java.io.Serializable;

public interface DistributionBarServerRpc extends Serializable {
    void onItemClicked(int var1, MouseEventDetails var2);
}
