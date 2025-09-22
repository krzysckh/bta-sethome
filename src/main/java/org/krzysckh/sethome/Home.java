/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */
package org.krzysckh.sethome;

import net.minecraft.core.util.phys.*;

public class Home {
  public String owner;
  public String name;
  public Vec3 place;
  public int dimension;

  public Home(String owner, String name, double x, double y, double z, int dimension) {
    this.owner = owner;
    this.name = name;
    this.place = Vec3.getPermanentVec3(x, y, z);
    this.dimension = dimension;
  }

  @Override
  public String toString() {
    return String.format("%s (%.2f, %.2f, %.2f)", this.name, this.place.x, this.place.y, this.place.z);
  }
}
