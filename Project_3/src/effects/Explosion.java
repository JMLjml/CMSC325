package effects;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * @author John M. Lasheski
 */
public class Explosion {
  private static float COUNT_FACTOR_F;
  private static boolean POINT_SPRITE;

  public static void triggerExplosion(Node targetNode, PhysicsCollisionEvent event, AssetManager assetManager) {

    ParticleEmitter explosion = new ParticleEmitter("My explosion effect", Type.Triangle, 30);
      
    explosion.setSelectRandomImage(true);
    explosion.setStartColor(new ColorRGBA(1f, 0.4f, 0.05f, (float) (1f / COUNT_FACTOR_F)));
    explosion.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
    explosion.setStartSize(1.3f);
    explosion.setEndSize(6f);
    explosion.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
    explosion.setParticlesPerSec(0);
    explosion.setGravity(0, -5, 0);
    explosion.setLowLife(.4f);
    explosion.setHighLife(.5f);
    explosion.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
    explosion.getParticleInfluencer().setVelocityVariation(1f);
    explosion.setImagesX(2);
    explosion.setImagesY(2);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    mat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
    mat.setBoolean("PointSprite", POINT_SPRITE);
    explosion.setMaterial(mat);

    targetNode.attachChild(explosion);
    explosion.setLocalTranslation(event.getNodeB().getLocalTranslation());
    explosion.emitAllParticles();

    // Play an explosion sounds
    Sounds.audio_explosion.playInstance();
  }
}
