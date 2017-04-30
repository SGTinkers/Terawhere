package tech.msociety.terawhere.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

public class CustomRadioGroup extends RadioGroup {
    public CustomRadioGroup(Context context) {
        super(context);
    }

    public CustomRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean hasCheckedRadioButton() {
        return !(getCheckedRadioButtonId() == -1);
    }
}
