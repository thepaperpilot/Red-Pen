package thepaperpilot.rpg;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class OrthogonalTiledMapRendererWithSprites extends OrthogonalTiledMapRenderer {

    public OrthogonalTiledMapRendererWithSprites(TiledMap map) {
        super(map);
    }

    @Override
    public void renderObject(MapObject object) {
        if(object instanceof TextureMapObject && object.isVisible()) {
            TextureMapObject textureObj = (TextureMapObject) object;
            batch.draw(textureObj.getTextureRegion(), (int) textureObj.getX(), (int) textureObj.getY());
        }
    }
}
