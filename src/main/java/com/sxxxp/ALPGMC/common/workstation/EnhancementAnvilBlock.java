package com.sxxxp.ALPGMC.common.workstation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// 바닐라 모루를 대체하는 강화 모루입니다. 바닐라 모루와 달리 떨어지지 않고 닳지 않습니다.
public class EnhancementAnvilBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape BASE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape X_SHAPE = Shapes.or(BASE,
            Block.box(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D),
            Block.box(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D),
            Block.box(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));
    private static final VoxelShape Z_SHAPE = Shapes.or(BASE,
            Block.box(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D),
            Block.box(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D),
            Block.box(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));

    public EnhancementAnvilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        player.openMenu(state.getMenuProvider(level, pos));
        return InteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, inventory, player) -> new EnhancementAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                Component.translatable("block.alpgmc.enhancement_anvil"));
    }
}
