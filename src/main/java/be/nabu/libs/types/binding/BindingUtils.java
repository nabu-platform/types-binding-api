package be.nabu.libs.types.binding;

public class BindingUtils {
	public static String camelCaseCharacter(String name, char character) {
		StringBuilder builder = new StringBuilder();
		int index = -1;
		int lastIndex = 0;
		boolean first = true;
		while ((index = name.indexOf(character, lastIndex)) >= lastIndex) {
			String substring = name.substring(lastIndex, index);
			if (substring.isEmpty()) {
				continue;
			}
			else if (first) {
				builder.append(substring);
				first = false;
			}
			else {
				builder.append(substring.substring(0, 1).toUpperCase() + substring.substring(1));
			}
			lastIndex = index + 1;
		}
		if (lastIndex <= name.length() - 1) {
			if (first) {
				builder.append(name.substring(lastIndex));
			}
			else {
				builder.append(name.substring(lastIndex, lastIndex + 1).toUpperCase()).append(name.substring(lastIndex + 1));
			}
		}
		return builder.toString();
	}
}
