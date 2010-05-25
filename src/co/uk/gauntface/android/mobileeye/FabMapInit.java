package co.uk.gauntface.android.mobileeye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FabMapInit extends Activity
{
	private Button mFabMapUseURLBtn;
	private Button mFabMapSkipBtn;
	private EditText mFabMapURLEditText;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fabmap_init);
        
        initActivity();
        executeActivity();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    
    protected void onStop()
    {
    	super.onStop();
    }

    protected void onDestroy()
    {
    	super.onDestroy();
    }
    
    private void initActivity()
    {
    	mFabMapUseURLBtn = (Button) findViewById(R.id.FabMapUseURLBtn);
    	mFabMapUseURLBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view)
			{
				String addr = validateAddr(mFabMapURLEditText.getText().toString());
				if(addr != null)
				{
					startNextActivity(addr);
				}
				else
				{
					Toast t = Toast.makeText(getApplicationContext(), "Address is not valid", Toast.LENGTH_LONG);
					t.show();
				}
			}
		});
    	
    	mFabMapSkipBtn = (Button) findViewById(R.id.FabMapSkipBtn);
    	mFabMapSkipBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0)
			{
				startNextActivity(null);
			}
		});
    	
    	mFabMapURLEditText = (EditText) findViewById(R.id.FabMapAddressEditText);
    }
    
    private void executeActivity()
    {
    	
    }
    
    private String validateAddr(String addr)
    {
    	addr = addr.trim();
    	if(addr.length() > 0)
    	{
    		return addr;
    	}
    	
    	return null;
    }
    
    private void startNextActivity(String addr)
    {
    	Singleton.setFabMapServerAddr(addr);
    	
    	Toast t = Toast.makeText(getApplicationContext(),
				"Starting Camera Activity",
				Toast.LENGTH_LONG);
		
		t.show();
		
		Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
		startActivity(intent);
		
		finish();
    }
}
