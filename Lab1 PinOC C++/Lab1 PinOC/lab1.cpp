#include <iostream>
#include <filesystem>

using namespace std;
using namespace filesystem;

void print_dir_path(const string& current_path, int level = 0) {
	for (const auto& path : directory_iterator(current_path)) {
		for (int i = 0; i < level; ++i) {
			std::cout << " |   ";
		}
		if (is_directory(path)) {
			cout << "[+]" << path.path().filename().string() << "\n";
			print_dir_path(path.path().string(), level + 1);
		}
		else {
			cout << path.path().filename().string() << endl;
		}
	}
	if (level != 0) {
		for (int i = 0; i < level - 1; ++i) {
			std::cout << " |   ";
		}
		cout << " +------" << endl;
	}
}

int main() {
	cout << "Directory tree for " << current_path().string() << "\n" << endl;
	print_dir_path(current_path().string());
	return 0;
}