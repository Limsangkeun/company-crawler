let stopFlag = false;
const createListItem = (text, index) => {
    const li = document.createElement("li");
    li.className = "list-group-item clearfix";
    li.innerHTML = `<span>${text}</span><button class="btn btn-default btn-xs pull-right remove-item" onclick="removeKeyword(${index})">
        <span class="bi bi-x" aria-hidden="true"></span></button>`;
    return li;
};

const addKeyword = () => {
    const iptKeyword = $("#ipt_keyword")[0];
    const currentKeyword = iptKeyword.value.trim();
    const keywordList = $("#keywordList")[0];

    if (currentKeyword === "") {
        alert("keyword를 입력하세요.");
        iptKeyword.value = "";
        return;
    }

    let arr = JSON.parse(localStorage.getItem("keywordList")) || [];
    arr.push(currentKeyword);
    localStorage.setItem("keywordList", JSON.stringify(arr));

    const li = createListItem(currentKeyword, arr.length - 1);
    keywordList.appendChild(li);

    iptKeyword.value = "";
    keywordList.scrollTop = keywordList.scrollHeight;
};

const removeKeyword = index => {
    let arr = JSON.parse(localStorage.getItem("keywordList")) || [];
    arr.splice(index, 1);
    localStorage.setItem("keywordList", JSON.stringify(arr));
    resetKeyword();
};

const resetKeyword = () => {
    $("#keywordList")[0].innerHTML = "";
};

const clearStatus = () => {
    $(".currentStatus")[0].innerText = "";
};

const stopCrawling = () => {
    if (stopFlag) {
        alert("이미 종료 대기 상태입니다.");
        return;
    }
    alert("다음 키워드부터 크롤링이 종료됩니다.");
    setStopSignal(true);
};

const setStopSignal = flag => {
    stopFlag = flag;
};

const startCrawling = () => {
    const keywordList = $("#keywordList")[0];
    const currentStatus = $("#currentStatus")[0];
    const startCrawlingBtn = $("#startCrawling")[0];
    const stopCrawlingBtn = $("#stopCrawling")[0];

    startCrawlingBtn.disabled = true;
    stopCrawlingBtn.disabled = false;

    if (keywordList.childElementCount === 0) {
        startCrawlingBtn.disabled = false;
        stopCrawlingBtn.disabled = true;
        alert("keyword가 없습니다.");
        return;
    }

    if (stopFlag) {
        alert("검색을 종료합니다.");
        startCrawlingBtn.disabled = false;
        stopCrawlingBtn.disabled = true;
        setStopSignal(false);
        return;
    }

    const keyword = keywordList.firstElementChild.querySelector("span").innerText;
    keywordList.removeChild(keywordList.firstElementChild);

    const li = document.createElement("li");
    li.innerText = `${keyword}로 검색을 시작합니다.`;
    currentStatus.appendChild(li);

    fetch("/startCrawling", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ 'keyword': encodeURIComponent(keyword) }),
    })
        .then(response => response.json())
        .then(data => {
            drawGrid(data.list);
        });
};

const drawGrid = list => {
    const tbody = $("#resultTableBody")[0];
    const currentStatus = $("#currentStatus")[0];
    tbody.innerHTML = '';

    if (!list || list.length === 0) {
        const emptyList = document.createElement('tr');
        emptyList.innerHTML = `<td colspan="6" style="text-align: center">해당 키워드로 검색된 데이터가 없습니다.</td>`;
        tbody.appendChild(emptyList);

        const li = document.createElement('li');
        li.textContent = `${list.length}개의 데이터 검색 완료`;
        currentStatus.appendChild(li);

        setTimeout(startCrawling, 5000);
        return;
    }

    const li = document.createElement('li');
    li.textContent = `${list.length}개의 데이터 검색 완료`;
    currentStatus.appendChild(li);

    for (let i = 0; i < list.length; i++) {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td style="text-align: center">${list[i].name}</td>
            <td>${list[i].category ? list[i].category.join(",") : '카테고리 없음.'}</td>
            <td>${list[i].tel}</td>
            <td>${list[i].address}</td>
            <td><div style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap; max-width: 450px;">${list[i].homePage}</div></td>`;
        tbody.appendChild(tr);
    }

    setTimeout(startCrawling, 5000);
};

const searchUsingCityList = () => {
    const cityKeywordEl = $('#cityKeyword');
    const keyword = cityKeywordEl.val().trim();
    if (!keyword) {
        alert("keyword를 입력하세요.");
        return;
    }

    const cityList = JSON.parse(localStorage.getItem('cityList'));
    if (!cityList) {
        alert("도시 목록이 비어 있습니다.");
        return;
    }

    for (let i = 0; i < cityList.length; i++) {
        addKeywordFromLocalStorage(`${cityList[i]} ${keyword}`);
    }

    cityKeywordEl.val('');
    $('#searchModal').modal('hide');
};

const addKeywordFromLocalStorage = keyword => {
    const li = document.createElement("li");
    li.className = "list-group-item clearfix";
    li.innerHTML = `<span>${keyword}</span><button class="btn btn-default btn-xs pull-right remove-item"></button>`;
    $('#keywordList').append(li);
};
const addCity = () => {
    const cityName = $('#cityName').val().trim();

    if (cityName !== "") {
        $('#cityModal').modal('hide'); // 모달 숨기기
        $('#cityName').val(''); // 입력 필드 비우기

        // 도시명을 추가할 목록에 항목 추가
        $('#keywordList').append(`<li class="list-group-item">${cityName}
            <button type="button" class="btn btn-sm btn-danger float-right" onclick="removeCity(this)">삭제</button></li>`);
    }
}