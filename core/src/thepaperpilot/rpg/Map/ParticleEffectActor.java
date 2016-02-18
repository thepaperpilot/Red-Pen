package thepaperpilot.rpg.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import thepaperpilot.rpg.Main;

public class ParticleEffectActor extends Actor {
    public ParticleEffect effect;

    public ParticleEffectActor(ParticleEffect effect, float x, float y) {
        this.effect = effect;
        if (effect != null)
            effect.start();
        setPosition(x, y);
    }

    public void draw(Batch batch, float parentAlpha) {
        effect.draw(batch);
    }

    public void act(float delta) {
        super.act(delta);
        effect.setPosition(getX(), getY());
        effect.update(delta);
    }

    public static class EnvironmentParticleEffect extends ParticleEffectActor{
        private Area area;

        public EnvironmentParticleEffect(String effect, final Area area) {
            super(null, 320, 180);
            this.area = area;

            ParticleEffect particleEffect = new ParticleEffect();
            particleEffect.load(Gdx.files.internal(effect + ".p"), Gdx.files.internal(""));
            particleEffect.scaleEffect(Main.TILE_SIZE / Math.max(area.prototype.mapSize.x, area.prototype.mapSize.y));
            for (int i = 0; i < 100; i++) {
                particleEffect.update(.1f);
            }
            this.effect = particleEffect;

            area.stage.addActor(this);
        }

        public void act(float delta) {
            super.act(delta);
            Vector3 pos = area.camera.position;
            effect.setPosition(-pos.x + 320, -pos.y + 180);
            effect.getEmitters().first().getXOffsetValue().setLow(pos.x);
            effect.getEmitters().first().getYOffsetValue().setLow(pos.y);
        }
    }
}
