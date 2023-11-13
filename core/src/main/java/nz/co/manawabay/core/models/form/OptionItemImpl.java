package nz.co.manawabay.core.models.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptionItemImpl implements com.adobe.cq.wcm.core.components.models.form.OptionItem {
    private String value;
    private String text;
    private boolean disabled;
    private boolean selected;
}
