#include <QApplication>
#include <QLabel>

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);
    QLabel label("Hello, Qt5 on Raspberry Pi4 (EGLFS)!");
    label.show();
    return app.exec();
}
