document.getElementById('simulateForm').addEventListener('submit', async function (e) {
    e.preventDefault();

    const data = {
        statusCode: parseInt(document.getElementById('statusCode').value) || null,
        delay: parseInt(document.getElementById('delay').value) || null,
        responseSize: parseInt(document.getElementById('responseSize').value) || null,
        baseMessage: document.getElementById('baseMessage').value || null,
        brokenJson: document.getElementById('brokenJson').checked,
        body: document.getElementById('body').value || null
    };

    try {
        await fetch('/configure', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        document.getElementById('result').textContent =
            "Configuration saved. Now your app can call /simulate";

    } catch (error) {
        document.getElementById('result').textContent =
            "Error: " + error;
    }
});