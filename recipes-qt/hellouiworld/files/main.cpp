#include <QApplication>
#include <QLabel>

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);
    QLabel label("Hello, Qt5 EGLFS!");
    label.setAlignment(Qt::AlignCenter);
    label.setStyleSheet("color: white; font: bold 32px; background-color: rgb(0,0,170);");
    label.showFullScreen(); // tärkeää EGLFS:lle
    return app.exec();
}
