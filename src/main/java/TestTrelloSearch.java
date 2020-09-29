import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

public class TestTrelloSearch {

	public static final String LIMIT = "100";

	public static final String BOARD = "5f71fd9dae37660676fd09b0";

	public static final int ITERATIONS = 10;

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < ITERATIONS; i++) {
			List<String> cardIds1 = getCardIds(0);
			List<String> cardIds2 = getCardIds(1);

			List<String> same = cardIds1.stream()
					.filter(cardIds2::contains)
					.collect(Collectors.toList());
			System.out.println("Same cards: " + same.toString());
		}
	}

	private static List<String> getCardIds(int pageNumber) {
		String page = getPage(pageNumber);
		DocumentContext jsonContext = JsonPath.parse(page);
		List<String> cardIds = jsonContext.read("$.cards[0:].id");
		return cardIds;
	}

	private static String getPage(int number) {
		String path = String.format("https://api.trello.com/1/search?query=is:open&cards_limit=" + LIMIT + "&cards_page=%d&idBoards=" + BOARD, number);
		return wget(path);
	}

	private static String wget(String url) {
		String s;
		InputStream is = null;
		String text = "";
		try {
			System.out.println("GET " + url);
			URL url1 = new URL(url);
			is = url1.openStream();
			text = IOUtils.toString(is, StandardCharsets.UTF_8.name());
			return text;
		} catch (MalformedURLException mue) {
			System.err.println("Ouch - a MalformedURLException happened.");
			mue.printStackTrace();
			System.exit(2);
		} catch (IOException ioe) {
			System.err.println("Oops- an IOException happened.");
			ioe.printStackTrace();
			System.exit(3);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}
		return text;
	}
}
