package ru.elron.examplevangogh.utils.vangogh

/**
 * Просто возвращает новые экземпляры
 */
class SingleContainerBuilder(_vangogh: Vangogh) : Container.IBuilder() {
    init {
        this.vangogh = _vangogh
    }

    override fun newInstance(): Container = Container(vangogh)

    override fun release(container: Container) = container.clear()
}