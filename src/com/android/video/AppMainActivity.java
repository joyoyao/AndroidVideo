package com.android.video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.android.video.page.EFRecordPageView;
import com.android.video.tools.E4FunTool;
import com.android.nxui.NXPageRoot;
import com.android.nxui.NXPageStack;

public class AppMainActivity extends Activity {

	private NXPageRoot			e4funPageRoot  = null ;
	private NXPageStack			e4funPageStack = null ;
	
	//private EFMainPageView		mainPageView  = null ;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		E4FunTool.printPhotoInfos() ;
		
		try {this.copyAssetToSDCard() ;}
		catch(Exception e) {e.printStackTrace();}
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE) ;
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ;

		String rootPath = String.format("%s%s", Environment.getExternalStorageDirectory().getAbsolutePath(), "/e4fun") ;
		EFRecordPageView mainPageView = new EFRecordPageView(this, rootPath) ;
		
		this.e4funPageRoot = new NXPageRoot(this);
		this.setContentView(this.e4funPageRoot);
		
		this.e4funPageStack = new NXPageStack(mainPageView, this);
		this.e4funPageRoot.pushPageStack(this.e4funPageStack, false);
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_main, menu);
		return false ;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void copyAssetToSDCard() throws Exception {
		String folderPath = String.format("%s%s", Environment.getExternalStorageDirectory().getAbsolutePath(), "/e4fun") ;
		File rootFolder = new File(folderPath) ;
		if(!rootFolder.exists())
			rootFolder.mkdirs() ;
		AssetManager assetManager = this.getAssets() ;
		copyAssetFolder(assetManager, "e4fun", folderPath);
	}
	
	private void copyAssetFolder(AssetManager assetManager, String srcfolder, String dstFolder) throws Exception {
		String[] subFiles = assetManager.list(srcfolder) ;
		for(String subFile : subFiles) {
			String srcSubPath = String.format("%s/%s", srcfolder, subFile) ;
			String dstSubPath = String.format("%s/%s", dstFolder, subFile) ;
			if(srcSubPath.indexOf('.') < 0) {
				new File(dstSubPath).mkdirs() ;
				copyAssetFolder(assetManager, srcSubPath, dstSubPath);
			}
			else {
				copyAssetFile(assetManager, srcSubPath, dstSubPath);
			}
		}
	}
	
	private void copyAssetFile(AssetManager assetManager, String srcFile, String dstPath) throws Exception {
		File distFile = new File(dstPath) ;
		if(distFile.exists())
			return ;
		InputStream in = assetManager.open(srcFile) ;
		if(in != null) {
			FileOutputStream out = new FileOutputStream(distFile) ;
			byte[] buf = new byte[1024] ;
			int    len = 0 ;
			while((len = in.read(buf, 0, 1024)) > 0) {
				out.write(buf, 0, len) ;
			}
			out.flush() ;
			out.close() ;
			in.close() ;
		}
	}
}
