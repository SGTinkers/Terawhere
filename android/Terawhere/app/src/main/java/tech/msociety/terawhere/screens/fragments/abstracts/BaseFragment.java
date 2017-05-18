package tech.msociety.terawhere.screens.fragments.abstracts;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected boolean needsProgressDialog = false;
    protected boolean needsEventBus = false;
    protected ProgressDialog progressDialog = null;
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (needsProgressDialog) {
            initProgressDialog();
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if (needsEventBus) {
            EventBus.getDefault().register(this);
        }
    }
    
    @Override
    public void onStop() {
        if (needsEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }
    
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Fetching data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }
}
