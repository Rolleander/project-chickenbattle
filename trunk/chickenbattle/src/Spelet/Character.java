package Spelet;
import Map.Chunk;
import Map.Map;
import Map.Voxel;
import Screens.Application;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.loaders.ModelLoaderRegistry;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedAnimation;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedModel;
import com.badlogic.gdx.graphics.g3d.model.keyframe.KeyframedSubMesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;


public class Character {
	public Vector3 position;
	public BoundingBox meshbox;
	public BoundingBox box;
	public Mesh model;
	public Matrix4 modelMatrix;
	public int weapon;
	public Array<Weapon> inventory;
	public boolean jumping;
	public Vector3 movement, oldPos;
	float forceUp;
	boolean hookshotting;
	public KeyframedModel charModel;
	public KeyframedModel charState;
	public KeyframedModel charChill;
	public KeyframedModel charDuck;
	public KeyframedModel charWalk;
	public KeyframedModel charDuckup;
	public KeyframedModel charJump;
	public KeyframedModel charLand;
	public KeyframedModel charStrafeLeft;
	public KeyframedModel charStrafeRight;
	public KeyframedAnimation animState;
	public KeyframedAnimation animChill;
	public KeyframedAnimation animDuck;
	public KeyframedAnimation animWalk;
	public KeyframedAnimation animDuckup;
	public KeyframedAnimation animJump;
	public KeyframedAnimation animLand;
	public KeyframedAnimation animStrafeLeft;
	public KeyframedAnimation animStrafeRight;
	public Texture modelTexture = null;
	public Character() {
		hookshotting = false;
		forceUp = 0;
		movement = new Vector3();
		oldPos = new Vector3();
		inventory = new Array<Weapon>();
		inventory.add(new Weapon(Weapon.gun));
		inventory.add(new Weapon(Weapon.ak));
		inventory.add(new Weapon(Weapon.block));
		weapon = inventory.get(0).weaponID;
		position = new Vector3();
		box = new BoundingBox();
		meshbox = new BoundingBox();
		modelMatrix = new Matrix4();
		charChill = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_chill.md2"));
		charDuck = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_duck.md2"));
		charDuckup = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_duckup.md2"));
		charWalk = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_walk.md2"));
		charJump = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_jump.md2"));
		charLand = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_land.md2"));
		charStrafeLeft = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_strafeleft.md2"));
		charStrafeRight = ModelLoaderRegistry.loadKeyframedModel(Gdx.files.internal("data/md2/chicken_straferight.md2"));
		charState = charChill;
		modelTexture = new Texture(Gdx.files.internal("data/md2/tmap.png"), Format.RGB565, true);
		charChill.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charDuck.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charDuckup.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charJump.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charLand.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charStrafeLeft.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		charStrafeRight.setMaterial(new Material("s_texture", new TextureAttribute(modelTexture, 0, "a_texCoord2")));
		animChill = (KeyframedAnimation)charChill.getAnimations()[0];
		animDuck = (KeyframedAnimation)charDuck.getAnimations()[0];
		animDuckup = (KeyframedAnimation)charDuckup.getAnimations()[0];
		animWalk = (KeyframedAnimation)charWalk.getAnimations()[0];
		animJump = (KeyframedAnimation)charJump.getAnimations()[0];
		animLand = (KeyframedAnimation)charLand.getAnimations()[0];
		animStrafeLeft = (KeyframedAnimation)charStrafeLeft.getAnimations()[0];
		animStrafeRight = (KeyframedAnimation)charStrafeRight.getAnimations()[0];

		modelTexture = new Texture(Gdx.files.internal("data/md2/tmap.png"), Format.RGB565, true);

		System.out.println("NORMALS?" + hasNormals());
		model = charState.subMeshes[0].mesh;
	}
	private boolean hasNormals () {
		for (KeyframedSubMesh mesh : charState.subMeshes) {
			if (mesh.mesh.getVertexAttribute(Usage.Normal) == null) return false;
		}
		return true;
	}
	public void updateModel() {
		model = charState.subMeshes[0].mesh;
	}
	public void setPos(float x, float y, float z) {
		
		position.set(x,y,z);
		modelMatrix = new Matrix4();
		modelMatrix.setToTranslation(position);
		model.calculateBoundingBox(meshbox);
		box.set(meshbox);
		box.mul(modelMatrix);
	}
	public void setPos(Vector3 pos) {
		position.set(pos);
		modelMatrix.setToTranslation(position);
		box.set(meshbox);
		box.mul(modelMatrix);
	}
	public void addMovement(Vector3 movement) {
		position.add(movement);
		modelMatrix.setToTranslation(position);
		box.set(meshbox);
		box.mul(modelMatrix);
	}
	public void ressurrect() {
		setPos(30,60,50);
		forceUp = 0;
		jumping = false;
		for (int i = 0; i < inventory.size; i++) {
			inventory.get(i).restart();
		}
	}
	public void update(Application app) {

		if (hookshotting) {

		} else {
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				charState = charWalk;
				oldPos.set(position);
				movement.set(app.cam.direction.x,0,app.cam.direction.z);
				movement.nor();
				movement.mul(Gdx.graphics.getDeltaTime()*10);
				addMovement(movement);

				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize].id == Voxel.grass) {
									setPos(oldPos);
								}		
								break;
							}
						}
					}
				}
			}
			if (Gdx.input.isKeyPressed((Input.Keys.S))) {
				charState = charWalk;
				oldPos.set(position);
				movement.set(app.cam.direction.x,0,app.cam.direction.z);
				movement.nor();
				movement.mul(Gdx.graphics.getDeltaTime()*10*-1);
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/app.map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize].id == Voxel.grass) {
									setPos(oldPos);
								}		
								break;
							}
						}
					}
				}
			}
			if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				charState = charStrafeRight;
				oldPos.set(position);
				movement.set(app.cam.direction.x,0,app.cam.direction.z);
				movement.crs(app.cam.up);
				movement.nor();
				movement.mul(Gdx.graphics.getDeltaTime()*10);
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize] .id == Voxel.grass) {
									setPos(oldPos);
								}		
								break;
							}
						}
					}
				}

			}
			if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				charState = charStrafeLeft;
				oldPos.set(position);
				movement.set(app.cam.direction.x,0,app.cam.direction.z);
				movement.crs(app.cam.up);
				movement.nor();
				movement.mul(Gdx.graphics.getDeltaTime()*-10);
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize] .id == Voxel.grass) {
									setPos(oldPos);
								}		
								break;
							}
						}
					}
				}

			}
			if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
				charState = charJump;
				oldPos.set(position);
				movement.set(0,-1*Gdx.graphics.getDeltaTime()*10,0);
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize] .id == Voxel.grass) {
									jumping = true;
									forceUp = 5;
								}		
								break;
							}
						}
					}
				}
				position.set(oldPos);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.R)) {
				inventory.get(weapon).reload();		
			}
			if (jumping) {
				oldPos.set(position);
				movement.set(0,Gdx.graphics.getDeltaTime()*10*forceUp,0);
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize] .id == Voxel.grass) {
									setPos(oldPos);
									jumping = false;
								}		
								break;
							}
						}
					}
					forceUp -= 2.5f*Gdx.graphics.getDeltaTime();
					if (forceUp < 0) {
						charState = charLand;
						jumping = false;
						forceUp = 0;
					}
				}

			} else {
				charState = charChill;
				oldPos.set(position);
				movement.set(0,Gdx.graphics.getDeltaTime()*10*forceUp,0);
				forceUp -= 2.5f*Gdx.graphics.getDeltaTime();
				addMovement(movement);
				for (Vector3 vec : box.getCorners()) {
					int pointX = (int) vec.x;
					int pointY = (int) vec.y;
					int pointZ = (int) vec.z;
					if (pointX >= 0 && pointX < Map.x && pointY >= 0 && pointY < Map.y && pointZ >= 0 && pointZ < Map.z) {
						for (Chunk c : app.map.chunks) {
							if (c.x == (pointX/Map.chunkSize) && c.y == (pointY/Map.chunkSize) && c.z == (pointZ/Map.chunkSize)) {
								if (c.map[pointX-c.x*Map.chunkSize][pointY-c.y*Map.chunkSize][pointZ-c.z*Map.chunkSize] .id == Voxel.grass) {
									setPos(oldPos);
									forceUp = 0;
								}		
								break;
							}
						}
					}
				}
			}
		}
	}
}
