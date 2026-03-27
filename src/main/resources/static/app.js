const brokenJsonCheckbox = document.getElementById('brokenJson');
const bodyTextArea = document.getElementById('body');

brokenJsonCheckbox.addEventListener('change', function () {
    if (this.checked) {
        bodyTextArea.disabled = true;
        bodyTextArea.placeholder = "Custom Body (disabled when Broken JSON is checked)";
        bodyTextArea.value = "";
    } else {
        bodyTextArea.disabled = false;
        bodyTextArea.placeholder = "Custom Body (optional)";
    }
});

document.getElementById('simulateForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const data = {
        statusCode: parseInt(document.getElementById('statusCode').value) || null,
        delay: parseInt(document.getElementById('delay').value) || null,
        responseSize: parseInt(document.getElementById('responseSize').value) || null,
        baseMessage: document.getElementById('baseMessage').value || null,
        brokenJson: brokenJsonCheckbox.checked,
        body: bodyTextArea.value || null,
        httpMethod: document.getElementById('httpMethod').value
    };

    try {
        await fetch('/configure', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const resultElement = document.getElementById('result');
        resultElement.textContent = "Configuration saved. Now your app can call /simulate";

        resultElement.classList.remove('updated-animation');
        void resultElement.offsetWidth;
        resultElement.classList.add('updated-animation');

    } catch (error) {
        document.getElementById('result').textContent = "Error: " + error;
    }
});