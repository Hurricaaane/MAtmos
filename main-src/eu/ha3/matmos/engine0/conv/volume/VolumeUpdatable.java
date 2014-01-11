package eu.ha3.matmos.engine0.conv.volume;

/*
--filenotes-placeholder
*/

public interface VolumeUpdatable extends VolumeContainer
{
	public void setVolumeAndUpdate(float volume);
	
	public void updateVolume();
}
