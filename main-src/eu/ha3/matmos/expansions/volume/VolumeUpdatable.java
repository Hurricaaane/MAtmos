package eu.ha3.matmos.expansions.volume;

/*
--filenotes-placeholder
*/

public interface VolumeUpdatable extends VolumeContainer
{
	public void setVolumeAndUpdate(float volume);
	
	public void updateVolume();
}
