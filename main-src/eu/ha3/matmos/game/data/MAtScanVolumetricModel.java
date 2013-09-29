package eu.ha3.matmos.game.data;

import eu.ha3.matmos.game.system.MAtMod;
import eu.ha3.mc.convenience.Ha3Signal;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtScanVolumetricModel
{
	private MAtMod mod;
	
	private MAtScanCoordsOps pipeline;
	
	private long xstart;
	private long ystart;
	private long zstart;
	
	private long xsize;
	private long ysize;
	private long zsize;
	
	private long opspercall;
	
	private boolean isScanning;
	
	private long finality;
	private long progress;
	
	private Ha3Signal onDone;
	
	public MAtScanVolumetricModel(MAtMod mod2)
	{
		this.mod = mod2;
		this.pipeline = null;
		this.isScanning = false;
		
	}
	
	public void setPipeline(MAtScanCoordsOps pipelineIn)
	{
		this.pipeline = pipelineIn;
		
	}
	
	public void startScan(
		long x, long y, long z, long xsizeIn, long ysizeIn, long zsizeIn, long opspercallIn, Ha3Signal onDoneIn) //throws MAtScannerTooLargeException
	{
		if (this.isScanning)
			return;
		
		if (this.pipeline == null)
			return;
		
		if (opspercallIn <= 0)
			throw new IllegalArgumentException();
		
		int worldHeight = this.mod.util().getWorldHeight();
		
		if (ysizeIn > worldHeight)
		{
			ysizeIn = worldHeight;
			//throw new MAtScannerTooLargeException();
		}
		
		this.xsize = xsizeIn;
		this.ysize = ysizeIn;
		this.zsize = zsizeIn;
		
		y = y - this.ysize / 2;
		
		if (y < 0)
		{
			y = 0;
		}
		else if (y > worldHeight - this.ysize)
		{
			y = worldHeight - this.ysize;
		}
		
		this.xstart = x - this.xsize / 2;
		this.ystart = y; // ((y - ysize / 2)) already done before
		this.zstart = z - this.zsize / 2;
		this.opspercall = opspercallIn;
		
		this.progress = 0;
		this.finality = this.xsize * this.ysize * this.zsize;
		
		this.pipeline.begin();
		this.isScanning = true;
		
	}
	
	public boolean routine()
	{
		if (!this.isScanning)
			return false;
		
		long ops = 0;
		long x, y, z;
		while (ops < this.opspercall && this.progress < this.finality)
		{
			// TODO Optimize this
			x = this.xstart + this.progress % this.xsize;
			z = this.zstart + this.progress / this.xsize % this.zsize;
			y = this.ystart + this.progress / this.xsize / this.zsize;
			
			this.pipeline.input(x, y, z);
			
			ops++;
			this.progress++;
			
		}
		
		if (this.progress >= this.finality)
		{
			scanDoneEvent();
		}
		return true;
		
	}
	
	public void stopScan()
	{
		this.isScanning = false;
		
	}
	
	private void scanDoneEvent()
	{
		if (!this.isScanning)
			return;
		
		this.pipeline.finish();
		stopScan();
		
		if (this.onDone != null)
		{
			this.onDone.signal();
		}
		
	}
	
}
