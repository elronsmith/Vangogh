package ru.elron.examplevangogh.utils.vangogh

import androidx.core.util.Pools

/**
 * Экземпляры не уничтожаются, а переиспользуются
 */
class PoolContainerBuilder(size: Int = 128) : Container.IBuilder() {
    val pool: Pools.SimplePool<Container>

    init {
        pool =
            Pools.SimplePool<Container>(
                size
            )
    }

    override fun newInstance(): Container = pool.acquire() ?: Container(
        vangogh
    )
    override fun release(container: Container) {
        container.clear()
        pool.release(container)
    }
}