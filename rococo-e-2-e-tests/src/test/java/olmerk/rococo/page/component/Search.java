package olmerk.rococo.page.component;

import static com.codeborne.selenide.Selenide.$;

public class Search extends BaseComponent<Search> {

    public Search() {
        super($("[type='search']"));
    }
}
