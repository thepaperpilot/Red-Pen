package thepaperpilot.rpg.Util;

import com.badlogic.ashley.core.ComponentMapper;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.*;

public class Mappers {
    public static final ComponentMapper<ActorComponent> actor = ComponentMapper.getFor(ActorComponent.class);
    public static final ComponentMapper<ChangeActorComponent> changeActor = ComponentMapper.getFor(ChangeActorComponent.class);
    public static final ComponentMapper<NameComponent> name = ComponentMapper.getFor(NameComponent.class);
    public static final ComponentMapper<PlayerComponent> playerController = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VisibleComponent> visible = ComponentMapper.getFor(VisibleComponent.class);
    public static final ComponentMapper<WalkableComponent> walkable = ComponentMapper.getFor(WalkableComponent.class);
    public static final ComponentMapper<AreaComponent> area = ComponentMapper.getFor(AreaComponent.class);
    public static final ComponentMapper<DialogueComponent> dialogue = ComponentMapper.getFor(DialogueComponent.class);
    public static final ComponentMapper<InventoryComponent> inventory = ComponentMapper.getFor(InventoryComponent.class);
    public static final ComponentMapper<MenuComponent> menu = ComponentMapper.getFor(MenuComponent.class);
    public static final ComponentMapper<FollowComponent> follow = ComponentMapper.getFor(FollowComponent.class);
    public static final ComponentMapper<IdleComponent> idle = ComponentMapper.getFor(IdleComponent.class);
    public static final ComponentMapper<WalkComponent> walk = ComponentMapper.getFor(WalkComponent.class);

    public static final ComponentMapper<TriggerComponent> trigger = ComponentMapper.getFor(TriggerComponent.class);
    public static final ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
    public static final ComponentMapper<EnterZoneComponent> enterZone = ComponentMapper.getFor(EnterZoneComponent.class);
    public static final ComponentMapper<LeaveZoneComponent> leaveZone = ComponentMapper.getFor(LeaveZoneComponent.class);
    public static final ComponentMapper<TargetComponent> target = ComponentMapper.getFor(TargetComponent.class);
}
