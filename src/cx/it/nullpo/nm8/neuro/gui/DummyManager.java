package cx.it.nullpo.nm8.neuro.gui;

import java.util.Collection;

public class DummyManager implements ScreenManager {

	@Override
	public void register(ScreenHolder s) {}

	@Override
	public Collection<ScreenHolder> getChangeTable() {
		return null;
	}

	@Override
	public void change(ScreenHolder s) {}

	@Override
	public void remove() {}

	@Override
	public void remove(ScreenHolder s) {}

	@Override
	public void removeAll() {}

	@Override
	public void render() {}

}
