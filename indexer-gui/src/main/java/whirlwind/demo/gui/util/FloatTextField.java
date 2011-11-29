package whirlwind.demo.gui.util;

import javax.swing.JTextField;

public class FloatTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	float min;
	float max;
	
	public FloatTextField(float min, float max) {
		super();
		this.min = min;
		this.max = max;
		installHandler();
	}
	public FloatTextField(float min, float max, Float value) {
		this(min, max);
		setFloat(value);
	}

	public Float getFloat() {
		if (getText().length() == 0) {
			return null;
		}
		return Float.valueOf(getText());
	}

	public void setFloat(Float value) {
		if (value != null) {
			setText(String.valueOf(value));
		} else {
			setText("");
		}
	}

	private void installHandler() {
		this.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(java.awt.event.KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '-' && getText().length() == 0) {
					return;
				}
				if (c < '0' || c > '9') {
					if (c != '.' || getText().contains("."))
						e.consume();
				}
				if (getFloat() == null) return;
				if (getFloat() < min) e.consume();
				if (getFloat() > max) e.consume();
			}
		});
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
}
