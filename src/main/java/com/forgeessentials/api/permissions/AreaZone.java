package com.forgeessentials.api.permissions;

import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WorldArea;
import com.forgeessentials.util.selections.WorldPoint;

/**
 * {@link AreaZone} covers just a specific area in one world. It has higher priority than all other {@link Zone} types. Area zones can overlap. Priority is then
 * decided by assigning highest priority to the innermost, smallest area.
 * 
 * @author Olee
 */
public class AreaZone extends Zone implements Comparable<AreaZone> {

	private WorldZone worldZone;

	private String name;

	private AreaBase region;

	private int priority;

	AreaZone(int id)
	{
		super(id);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area, int id)
	{
		this(id);
		this.worldZone = worldZone;
		this.name = name;
		this.region = area;
		this.worldZone.addAreaZone(this);
	}

	public AreaZone(WorldZone worldZone, String name, AreaBase area)
	{
		this(worldZone, name, area, worldZone.getServerZone().nextZoneID());
	}

	protected boolean isPointInZone(Point point)
	{
		return point.getX() >= region.getLowPoint().getX() && point.getZ() >= region.getLowPoint().getZ() && point.getX() <= region.getHighPoint().getX()
				&& point.getZ() <= region.getHighPoint().getZ() && point.getY() >= region.getLowPoint().getY() && point.getY() <= region.getHighPoint().getY();
	}

	@Override
	public boolean isInZone(WorldPoint point)
	{
		if (!worldZone.isInZone(point))
			return false;
		return isPointInZone(point);
	}

	@Override
	public boolean isInZone(WorldArea area)
	{
		if (!worldZone.isInZone(area))
			return false;
		return isPointInZone(area.getLowPoint()) && isPointInZone(area.getHighPoint());
	}

    @Override
    public boolean isPartOfZone(WorldArea area)
    {
        if (!worldZone.isPartOfZone(area))
            return false;
        return this.region.intersectsWith(area);
    }

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return worldZone.getName() + "_" + name;
	}

	@Override
	public Zone getParent()
	{
		// TODO: Get zones covering this one!
		return worldZone;
	}

	@Override
	public ServerZone getServerZone()
	{
		return worldZone.getServerZone();
	}
	
	public String getShotName()
	{
		return name;
	}

	public WorldZone getWorldZone()
	{
		return worldZone;
	}

	public AreaBase getArea()
	{
		return region;
	}

	public void setArea(AreaBase area)
	{
		this.region = area;
		getWorldZone().sortAreaZones();
	}

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	@Override
	public int compareTo(AreaZone area)
	{
		int cmp = area.priority - priority;
		if (cmp != 0) return cmp;
		
		Point areaSize = area.getArea().getSize();
		Point thisSize = getArea().getSize();
		cmp = (thisSize.getX() * thisSize.getY()) - (areaSize.getX() * areaSize.getY());
		if (cmp != 0) return cmp;
		
		return cmp;
	}

}
